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
            $http.get(Urls.Meals).
                then(function(response) {
                   $scope.meals = response.data;
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
        .controller('mealDetailController', ['$scope', '$http', '$routeParams', '$mdDialog', '$mdToast', function ($scope, $http, $routeParams, $mdDialog, $mdToast) {
            var mealDetailId = $routeParams.mealId;

            $http.get(Urls.Meals + '?id=' + mealDetailId).
                then(function(response) {
                    var meal = response.data;

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

            $scope.backToMeals = function () {
                Utils.appRedirectTo(Urls.Meals);
            };

            // this functions adds the current meal to the user's journal
            $scope.addJournal = function (ev) {
                if ($scope.addMealForm.$valid) {
                    if ($scope.calories > Constants.CalorieLimit) {
                        Utils.showAlert($mdDialog, ev, Strings.JOURNALS_CALORIES_GREATER_THAN_200_TITLE, Strings.JOURNALS_CALORIES_GREATER_THAN_200_DESCRIPTION);
                    } else {
                        var journalData = {
                            quantity : $scope.quantity,
                            mealId : $routeParams.mealId
                        };

                        $http.post(Urls.Journals, journalData).
                            then(function(response) {
                                Utils.showToast($mdToast, Strings.JOURNALS_JOURNAL_ADDED);
                                Utils.appRedirectTo(Urls.Journals);
                            }, function(response) {
                                if (response.status >= 400 && response.status < 500) {
                                    Utils.showAlert($mdDialog, ev, response.statusText, response.data);
                                } else {
                                    Utils.showAlert($mdDialog, ev, Strings.ERROR_500_TITLE, Strings.ERROR_500_DESCRIPTION);
                                }
                            });
                    }
                } else {
                    Utils.showAlert($mdDialog, ev, Strings.JOURNALS_DATA_INVALID_TITLE, Strings.JOURNALS_DATA_INVALID_DESCRIPTION);
                }
            };
        }])
        .controller('journalsController', ['$scope', '$http', 'journals', function ($scope, $http, journals) {
            $scope.journals = [];
            $scope.rawJournals = [];

            $http.get(Urls.Journals).
                then(function(response) {
                    $scope.journals = response.data;
                    $scope.rawJournals = journals.ungroup(response.data);
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

            $http.get(Urls.Journals + '?id=' + journalId).
                then(function(response) {
                    var journal = response.data;

                    $scope.currentJournal = journal;
                    $scope.unit = journal.unit;
                    $scope.calories = (journal.calories * journal.quantity).toFixed(2);
                    $scope.quantity = journal.quantity;

                    $scope.ui = {
                        'toolbarLabel': journal.name
                    };

                    $scope.updateCalories = function () {
                        if ($scope.editJournal.quantity.$valid) {
                            $scope.calories = ($scope.quantity * journal.calories).toFixed(2);
                        }
                    };
                });

            $scope.backToJournal = function () {
                Utils.appRedirectTo(Urls.Journals);
            };

            $scope.deleteJournal = function (ev) {
                // Appending dialog to document.body to cover sidenav in docs app
                var confirm = $mdDialog
                                .confirm()
                                .parent(angular.element(document.body))
                                .title(Strings.JOURNALS_DELETE_JOURNAL_QUESTION)
                                .content(Strings.JOURNALS_DELETE_JOURNAL_DESCRIPTION)
                                .ariaLabel(Strings.JOURNALS_DELETE_JOURNAL_QUESTION)
                                .ok(Strings.JOURNALS_DELETE_JOURNAL_CONFIRM)
                                .cancel(Strings.JOURNALS_DELETE_JOURNAL_CANCEL)
                                .targetEvent(ev);

                $mdDialog.show(confirm).then(function () {
                    $http.delete(Urls.Journals + '?id=' + journalId)
                        .then(function () {
                            Utils.showToast($mdToast, Strings.JOURNALS_JOURNAL_DELETED);
                        }, function(response) {
                            if (response.status >= 400 && response.status < 500) {
                                Utils.showAlert($mdDialog, ev, response.statusText, response.data);
                            } else {
                                Utils.showAlert($mdDialog, ev, Strings.ERROR_500_TITLE, Strings.ERROR_500_DESCRIPTION);
                            }
                        });
                    Utils.appRedirectTo(Urls.Journals);
                }, function () {
                    // user pressed cancel, do nothing
                });
            };

            $scope.updateJournal = function (ev) {
                if ($scope.editJournal.$valid) {
                    if ($scope.calories > Constants.CalorieLimit) {
                        Utils.showAlert($mdDialog, ev, Strings.JOURNALS_CALORIES_GREATER_THAN_200_TITLE, Strings.JOURNALS_CALORIES_GREATER_THAN_200_DESCRIPTION);
                    } else {
                        $scope.currentJournal.quantity = $scope.quantity;
                        var data = {
                            mealId: "" + $scope.currentJournal.mealId,
                            mealJournalId: "" + $scope.currentJournal.mealJournalId,
                            quantity: "" + $scope.currentJournal.quantity
                        };

                        $http.put(Urls.Journals, data).
                            then(function () {
                                Utils.showToast($mdToast, Strings.JOURNALS_JOURNAL_UPDATED);
                                Utils.appRedirectTo(Urls.Journals);
                            }, function(response) {
                                if (response.status >= 400 && response.status < 500) {
                                    Utils.showAlert($mdDialog, ev, response.statusText, response.data);
                                } else {
                                    Utils.showAlert($mdDialog, ev, Strings.ERROR_500_TITLE, Strings.ERROR_500_DESCRIPTION);
                                }
                            });
                    }
                } else {
                    Utils.showAlert($mdDialog, ev, Strings.JOURNALS_DATA_INVALID_TITLE, Strings.JOURNALS_DATA_INVALID_DESCRIPTION);
                }
            };
        }]);
}());