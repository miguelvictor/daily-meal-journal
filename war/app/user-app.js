(function () {
    'use strict';
    angular
        .module('MealJournalApp', ['ngMaterial', 'ngRoute', 'ngMessages'])
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when(Urls.Journals, {
                    'templateUrl': 'user/journals.html',
                    'controller': 'journalsController'
                })
                .when(Urls.JournalDetail, {
                    'templateUrl': 'user/journal_detail.html',
                    'controller': 'journalDetailController'
                })
                .when(Urls.Meals, {
                    'templateUrl': 'user/meals.html',
                    'controller': 'mealsController'
                })
                .when(Urls.MealDetail, {
                    'templateUrl': 'user/meal_detail.html',
                    'controller': 'mealDetailController'
                })
                .otherwise({
                    'redirectTo': Urls.Journals
                });
        }])
        .config(function ($mdThemingProvider) {
            $mdThemingProvider
                .theme('default')
                .primaryPalette(AppTheme.PrimaryPalette)
                .accentPalette(AppTheme.AccentPalette);
        })
        .config(['$mdIconProvider', function ($mdIconProvider) {
            $mdIconProvider
                .iconSet('action', 'svg/action-icons.svg')
                .iconSet('device', 'svg/device-icons.svg')
                .iconSet('navigation', 'svg/navigation-icons.svg')
                .iconSet('content', 'svg/content-icons.svg')
                .iconSet('alert', 'svg/alert-icons.svg')
                .iconSet('image', 'svg/image-icons.svg')
                .defaultIconSet('svg/core-icons.svg');
        }])
        .service('journals', function () {
        	return {
        		ungroup: function (journals) {
        			var ungrouped_journals = [];
        			
        			for (var i = 0, j; i < journals.length; i++) {
        				var a = journals[i].journals;
        				for (j = 0; j < a.length; j++) {
        					ungrouped_journals.push(a[j]);
        				}
        			}

        			return ungrouped_journals;
        		}
        	};
        })
        .controller('defaultController', ['$scope', '$mdSidenav', '$window', function ($scope, $mdSidenav, $window) {
            $scope.toggleSidenav = function (menuId) {
                $mdSidenav(menuId).toggle();
            };
            $scope.navigateTo = function (where) {
                Utils.appRedirectTo(where);
            };
            $scope.gotoHome = function () {
                Utils.redirectTo('/');
            };
        }])
        .controller('mealsController', ['$scope', '$http', function ($scope, $http) {
            $http
                .get(Urls.Meals)
                .success(function (response) {
                    $scope.meals = response;
                });

            $scope.openMeal = function (meal) {
                Utils.appRedirectTo('/meals/' + meal.mealId);
            };

            $scope.openSearch = function () {
            	$scope.searchEnabled = true;
            };
            
            $scope.closeSearch = function () {
            	$scope.searchEnabled = false;
            };
        }])
        .controller('mealDetailController', ['$scope', '$http', '$httpParamSerializerJQLike', '$routeParams', '$mdDialog', '$mdToast', function ($scope, $http, $httpParamSerializerJQLike, $routeParams, $mdDialog, $mdToast) {
            $http
                .get(Urls.Meals + '?id=' + $routeParams.mealId)
                .success(function(response) {
                    var meal = response;

                    $scope.currentMeal = meal;

                    $scope.unit = meal.unit;
                    $scope.calories = meal.calories;
                    $scope.quantity = meal.defaultQuantity;

                    $scope.updateCalories = function () {
                        if ($scope.addMealForm.quantity.$valid) {
                            $scope.calories = $scope.quantity * (meal.calories / meal.defaultQuantity);
                        }
                    };

                    $scope.ui = {
                        'toolbarLabel': meal.name
                    };
                });

            $scope.getToastPosition = function () {
                return Object.keys(Config.ToastPosition)
                    .filter(function (pos) {
                        return Config.ToastPosition[pos];
                    })
                    .join(' ');
            };

            $scope.backToMeals = function () {
                Utils.appRedirectTo(Urls.Meals);
            };

            // this functions adds the current meal to the user's journal
            $scope.addJournal = function (ev) {
                if ($scope.addMealForm.$valid) {
                    if ($scope.calories > Constants.CalorieLimit) {
                        $mdDialog.show(
                            $mdDialog
                                .alert()
                                .parent(angular.element(document.body))
                                .title(Strings.JOURNALS_CALORIES_GREATER_THAN_200_TITLE)
                                .content(Strings.JOURNALS_CALORIES_GREATER_THAN_200_DESCRIPTION)
                                .ariaLabel(Strings.JOURNALS_CALORIES_GREATER_THAN_200_TITLE)
                                .ok(Strings.JOURNALS_CALORIES_GREATER_THAN_200_OK_LABEL)
                                .targetEvent(ev)
                        );
                    } else {
                        var journalData =
                            {
                                'quantity' : $scope.quantity,
                                'mealId' : $routeParams.mealId
                            };

                        $http
                            .post(Urls.Journals, journalData)
                            .success(function(data) {
                                $mdToast.show(
                                    $mdToast
                                        .simple()
                                        .content(Strings.JOURNALS_JOURNAL_ADDED)
                                        .hideDelay(Config.ToastDelay)
                                        .position($scope.getToastPosition())
                                );
                                Utils.appRedirectTo(Urls.Journals);
                            })
                            .error(function() {
                                var alert = $mdDialog.alert()
                                    .parent(angular.element(document.body))
                                    .title(Strings.ERROR_500_TITLE)
                                    .content(Strings.ERROR_500_DESCRIPTION)
                                    .ariaLabel(Strings.ERROR_500_TITLE)
                                    .ok(Strings.ERROR_500_OK_LABEL)
                                    .targetEvent(ev);
                                
                                $mdDialog.show(alert).then(function () {
                                    // do nothing
                                });
                            });
                    }
                } else {
                    var alert = $mdDialog.alert()
                        .parent(angular.element(document.body))
                        .title('Some data inputted are invalid/missing.')
                        .content('You have to fix the errors before adding this journal.')
                        .ariaLabel('Adding a meal to a journal')
                        .ok('Okay, I\'ll fix it.')
                        .targetEvent(ev);

                    $mdDialog.show(alert).then(function () {
                        // do nothing
                    });
                }
            };
        }])
        .controller('journalsController', ['$scope', '$http', 'journals', function ($scope, $http, journals) {
            // getting journals

            $scope.journals = [];
            $scope.rawJournals = [];

            $http
                .get(Urls.Journals)
                .success(function (response) {
                    $scope.journals = response;
                    $scope.rawJournals = journals.ungroup(response);
                });

            $scope.openJournal = function (journal) {
                Utils.appRedirectTo(Urls.Journals + '/' + journal.mealJournalId);
            };

            $scope.goToMeals = function () {
                Utils.appRedirectTo(Urls.Meals);
            };
            
            $scope.closeSearch = function () {
            	$scope.searchEnabled = false;
            };
            
            $scope.openSearch = function () {
            	$scope.searchEnabled = true;
            };
        }])
        .controller('journalDetailController', ['$scope', '$http', '$routeParams', '$mdDialog', '$mdToast', function ($scope, $http, $routeParams, $mdDialog, $mdToast) {

            var journalId = $routeParams.mealJournalId

            $http
                .get('/journals?id=' + journalId)
                .success(function(response) {
                    $scope.currentJournal = response;
                    $scope.unit = response.unit;
                    $scope.calories = response.calories * response.quantity;
                    $scope.quantity = response.quantity;

                    $scope.ui = {
                        'toolbarLabel': response.name
                    };
                });

            $scope.getToastPosition = function () {
                return Object.keys(Config.ToastPosition)
                    .filter(function (pos) {
                        return Config.ToastPosition[pos];
                    })
                    .join(' ');
            };

            $scope.backToJournal = function () {
                Utils.appRedirectTo(Urls.Journals);
            };

            $scope.updateCalories = function () {
                if ($scope.editJournal.quantity.$valid) {
                    $scope.calories = $scope.quantity * journal.calories;
                }
            };

            $scope.deleteJournal = function (ev) {
                // Appending dialog to document.body to cover sidenav in docs app
                var confirm = $mdDialog
                                .confirm()
                                .parent(angular.element(document.body))
                                .title('Are you sure you want to delete this journal?')
                                .content('This action cannot be undone.')
                                .ariaLabel('Lucky day')
                                .ok('Delete')
                                .cancel('Cancel')
                                .targetEvent(ev);
                $mdDialog.show(confirm).then(function () {
                    $http
                        .delete('/journals?id=' + journalId)
                        .success(function () {
                            $mdToast.show(
                                $mdToast
                                    .simple()
                                    .content('Journal Deleted!')
                                    .hideDelay(Config.ToastDelay)
                                    .position($scope.getToastPosition())
                            );
                        });
                    Utils.appRedirectTo(Urls.Journals);
                }, function () {
                    // do nothing
                });
            };

            $scope.updateJournal = function (ev) {
                if ($scope.editJournal.$valid) {
                    if ($scope.calories > 2000) {
                        $mdDialog.show(
                            $mdDialog
                                .alert()
                                .parent(angular.element(document.body))
                                .title('Too much calories!')
                                .content('Dude, you must control yourself. Calorie count exceeds 2,000 and you are therefore not allowed to eat this thing. Sorry!')
                                .ariaLabel('Too much calories!')
                                .ok('Got it!')
                                .targetEvent(ev)
                        );
                    } else {
                        $scope.currentJournal.quantity = $scope.quantity;

                        $http
                            .put(Urls.Journals, {
                                mealId: $scope.currentJournal.mealId,
                                quantity: $scope.quantity,
                                mealJournalId: $scope.currentJournal.mealJournalId
                            })
                            .success(function () {
                                 $mdToast.show(
                                    $mdToast
                                        .simple()
                                        .content('Journal Updated!')
                                        .hideDelay(1000)
                                        .position($scope.getToastPosition())
                                );
                            });

                        Utils.appRedirectTo(Urls.Journals);
                    }
                } else {
                    var confirm = $mdDialog.confirm()
                        .parent(angular.element(document.body))
                        .title('Some data inputted are invalid/missing.')
                        .content('You have to fix the errors before updating your journal.')
                        .ariaLabel('Updating a meal in a journal')
                        .ok('Okay, I\'ll fix it.')
                        .targetEvent(ev);

                    $mdDialog.show(confirm).then(function () {
                        // do nothing
                    });
                }
            };
        }]);
}());