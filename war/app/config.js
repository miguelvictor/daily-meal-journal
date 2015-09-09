var AppTheme = {
	PrimaryPalette: 'indigo',
	AccentPalette: 'pink'
};

var Config = {
    ToastDelay: 1000,
    ToastPosition: 'bottom right'
};

var Urls = {
	Meals 			: '/meals',
	MealDetail	 	: '/meals/:mealId',
	MealsAdd		: '/meals/add',
	Journals 		: '/journals',
	JournalDetail 	: '/journals/:mealJournalId'
};

var Utils = {
	appRedirectTo: function (url) {
		window.location = window.location.href.split('#')[0] + '#' + url;
	},
	redirectTo: function (url) {
		if (url.length != 0) {
			if (url[0] === '/') {
				window.location = window.location.origin + url;
			} else {
				window.location = window.location.href + '/' + url;
			}
		}
	},
	isNumeric: function (string) {
		return /^\d+$/g.test(string);
	},
	showToast: function ($mdToast, message) {
		$mdToast.show(
            $mdToast.simple()
	            .content(message)
	            .hideDelay(Config.ToastDelay)
	            .position(Config.ToastPosition)
        );
	},
	showAlert: function ($mdDialog, event, title, body) {
	    $mdDialog.show(
	    	$mdDialog.alert()
	            .title(title)
	            .content(body)
	            .ariaLabel(title)
	            .ok('Okay')
	            .targetEvent(event)
	    );
	}
};

var Constants = {
	CalorieLimit: 2000
};

var Strings = {
	JOURNALS_CALORIES_GREATER_THAN_200_TITLE: 'Too much calories!',
	JOURNALS_CALORIES_GREATER_THAN_200_DESCRIPTION: 'Dude, you must control yourself. Calorie count exceeds 2,000 and you are therefore not allowed to eat this thing. Sorry!',
	JOURNALS_CALORIES_GREATER_THAN_200_OK_LABEL: 'Got it!',
	JOURNALS_JOURNAL_ADDED: 'Journal Added!',

	ERROR_500_TITLE: 'Internal Server Error',
	ERROR_500_DESCRIPTION: 'For some reason, there\'s something wrong with the server.',

	MEALS_ADD_MEAL_TOOLBAR_LABEL: 'Add Meal',
	MEALS_DELETE_MEAL_QUESTION: 'Are you sure you want to delete this meal?',
	MEALS_DELETE_MEAL_DESCRIPTION: 'This action cannot be undone.',
	MEALS_DELETE_MEAL_CONFIRM: 'Delete',
	MEALS_DELETE_MEAL_CANCEL: 'Cancel',
	MEALS_MEAL_ADDED: 'Meal Added!',
	MEALS_MEAL_DELETED: 'Meal Deleted!',
	MEALS_MEAL_UPDATED: 'Meal Updated!',
	MEALS_DATA_INVALID_TITLE: 'Oops!',
	MEALS_DATA_INVALID_DESCRIPTION: 'Some data inputted are invalid/missing. You have to fix the errors before adding the meal.'
};