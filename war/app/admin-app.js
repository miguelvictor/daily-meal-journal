(function () {
    'use strict';
    var app = angular.module('MealManagementApp', ['ngMaterial', 'ngRoute', 'ngMessages']);

    app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when(Urls.Meals, {
                templateUrl: 'admin/meals.html',
                controller: 'adminMealsController'
            })
            .when(Urls.MealsAdd, {
                templateUrl: 'admin/meal_add.html',
                controller: 'adminAddMealController'
            })
            .when(Urls.MealDetail, {
                templateUrl: 'admin/meal_edit.html',
                controller: 'adminEditMealController'
            })
            .otherwise({
                redirectTo: Urls.Meals
            });
    }]);

    app.config(function ($mdThemingProvider) {
        $mdThemingProvider
            .theme('default')
            .primaryPalette(AppTheme.PrimaryPalette)
            .accentPalette(AppTheme.AccentPalette);
    });

    app.config(['$mdIconProvider', function ($mdIconProvider) {
        $mdIconProvider
            .iconSet('action', 'svg/action-icons.svg')
            .iconSet('device', 'svg/device-icons.svg')
            .iconSet('navigation', 'svg/navigation-icons.svg')
            .iconSet('content', 'svg/content-icons.svg')
            .iconSet('alert', 'svg/alert-icons.svg')
            .iconSet('image', 'svg/image-icons.svg')
            .defaultIconSet('svg/core-icons.svg');
    }]);

    app.controller('defaultController', ['$scope', '$mdSidenav', function ($scope, $mdSidenav, $mdDialog) {
        $scope.toggleSidenav = function (menuId) {
            $mdSidenav(menuId).toggle();
        };
        $scope.navigateTo = function (where) {
            Utils.appRedirectTo(where);
        };
        $scope.gotoHome = function () {
            Utils.redirectTo('/');
        };
    }]);

    // MEALS LISTING
    app.controller('adminMealsController', ['$scope', '$http', function ($scope, $http) {
        $http.get(Urls.Meals).
            then(function(response) {
                $scope.meals = response.data;
            }, function() {
                // internal server error only can happen here, and it should not happen
                // but should it happen, let's just log it to console, because it's the server's fault and we can't do something about it except to tell the webmasters to look at their code and see what's missing
                console.error('Error loading meals');
            });

        $scope.openSearch = function () {
        	$scope.searchEnabled = true;
        };
        
        $scope.closeSearch = function () {
        	$scope.searchEnabled = false;
        };

        // action performed when a meal on the list is clicked
        $scope.openMeal = function (meal) {
            Utils.appRedirectTo('/meals/' + meal.mealId);
        };

        // FAB action
        $scope.addNewMeal = function () {
            Utils.appRedirectTo('/meals/add');
        };
    }]);

    // MEAL DETAILS
    app.controller('adminEditMealController', ['$scope', '$http', '$mdDialog', '$mdToast', '$routeParams', '$scope', function ($scope, $http, $mdDialog, $mdToast, $routeParams) {
        var mealId = $routeParams.mealId;

        // a simple trap to filter invalid IDs
        if (!Utils.isNumeric(mealId)) {
            Utils.appRedirectTo(Urls.Meals);
        }

        $scope.ui = {
            toolbarLabel: 'Loading Meal'
        };

        $http
            .get(Urls.Meals + '?id=' + mealId)
            .success(function (meal) {
                $scope.mealName = meal.name;
                $scope.defaultQuantity = meal.defaultQuantity + '';
                $scope.unit = meal.unit;
                $scope.calories = meal.calories + '';

                $scope.ui.toolbarLabel = 'Edit ' + meal.name;
            });

        $scope.backToMeals = function () {
            Utils.appRedirectTo(Urls.Meals);
        };

        $scope.deleteMeal = function (ev) {
            var confirm = $mdDialog.confirm()
                .title(Strings.MEALS_DELETE_MEAL_QUESTION)
                .content(Strings.MEALS_DELETE_MEAL_DESCRIPTION)
                .ariaLabel(Strings.MEALS_DELETE_MEAL_QUESTION)
                .ok(Strings.MEALS_DELETE_MEAL_CONFIRM)
                .cancel(Strings.MEALS_DELETE_MEAL_CANCEL)
                .targetEvent(ev);

            $mdDialog.show(confirm).then(function () {
                $http.delete(Urls.Meals + '?id=' + mealId).
                    then(function(response) {
                        Utils.showToast($mdToast, Strings.MEALS_MEAL_DELETED);
                        Utils.appRedirectTo(Urls.Meals);
                    }, function(response) {
                        if (response.status >= 400 && response.status < 500) {
                            Utils.showAlert($mdDialog, ev, response.statusText, response.data);
                        } else {
                            Utils.showAlert($mdDialog, ev, Strings.ERROR_500_TITLE, Strings.ERROR_500_DESCRIPTION);
                        }
                    });
            }, function () {
                // the user pressed cancel, do nothing
            });
        };

        $scope.updateMeal = function (ev) {
            if ($scope.editMealForm.$valid) {
                var mealData = {
                    calories : $scope.calories,
                    defaultQuantity : $scope.defaultQuantity,
                    unit : $scope.unit,
                    name : $scope.mealName,
                    mealId : mealId
                };

                $http.put(Urls.Meals, mealData)
                    .then(function(response) {
                        Utils.showToast($mdToast, Strings.MEALS_MEAL_UPDATED);
                        Utils.appRedirectTo(Urls.Meals);
                    }, function(response) {
                        if (response.status >= 400 && response.status < 500) {
                            Utils.showAlert($mdDialog, ev, response.statusText, response.data);
                        } else {
                            Utils.showAlert($mdDialog, ev, Strings.ERROR_500_TITLE, Strings.ERROR_500_DESCRIPTION);
                        }
                    });
            } else {            
                Utils.showAlert($mdDialog, ev, Strings.MEALS_DATA_INVALID_TITLE, Strings.MEALS_DATA_INVALID_DESCRIPTION);
            }
        };
    }]);

    app.controller('adminAddMealController', ['$scope', '$http', '$mdToast', '$mdDialog', function ($scope, $http, $mdToast, $mdDialog) {
        $scope.ui = {
            'toolbarLabel': Strings.MEALS_ADD_MEAL_TOOLBAR_LABEL
        };

        $scope.addMeal = function (ev) {
            if ($scope.addMealForm.$valid) {
                var mealData ={
                    calories : $scope.calories,
                    defaultQuantity : $scope.defaultQuantity,
                    unit : $scope.unit,
                    name : $scope.mealName
                };

                $http
                    .post(Urls.Meals, mealData).
                    then(function(response) {
                        Utils.showToast($mdToast, Strings.MEALS_MEAL_ADDED);
                        Utils.appRedirectTo(Urls.Meals);
                    }, function(response) {
                        if (response.status >= 400 && response.status < 500) {
                            Utils.showAlert($mdDialog, ev, response.statusText, response.data);
                        } else {
                            Utils.showAlert($mdDialog, ev, Strings.ERROR_500_TITLE, Strings.ERROR_500_DESCRIPTION);
                        }
                    });
            } else {
                Utils.showAlert($mdDialog, ev, 'Some data inputted are invalid/missing.', 'You have to fix the errors before adding the meal.');
            }
        };

        $scope.backToMeals = function () {
            Utils.appRedirectTo(Urls.Meals);
        };
    }]);
})();