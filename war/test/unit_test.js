describe('Group meals', function () {
    var mealService = null;

    beforeEach(module('MealJournalApp'));

    beforeEach(angular.mock.inject(function (meals) {
        mealService = meals;
    }));

});

describe('Meal Management Screen : Functions', function () {

    beforeEach(module('MealManagementApp'));

    describe('Add Meal Information', function () {

        it('should fail if calorie count is invalid', function () {

        });

        it('should pass if the calorie count is valid', function () {

        });
        
        it('should fail if the quantity is not numeric', function () {

        });

        it('should pass if the quantity is numeric', function () {

        });

        it('should fail if there is no unit provided', function () {

        });

        it('should pass if there is unit provided', function () {

        });

        it('should fail if meal is alreay added', function () {

        });

        it('should pass if meal is not yet added', function () {

        });

    });

});