require.config({
    baseUrl: "/",
    paths: {
        app: "../app",
        jquery: ieVersion <= 9 ? "jquery-1.11.0.min" : "jquery-2.1.1.min",
        bootbox: "assets/plugins/ui/bootbox/bootbox",
        excanvas: "assets/js/libs/excanvas.min",
        html5shiv: "assets/js/html5shiv",
        SoundManager: "soundmanager2/soundmanager2-nodebug-jsmin",
        "jquery.autosize": "assets/plugins/forms/autosize/jquery.autosize.min",
        "jquery.bootstrap": "assets/js/bootstrap/bootstrap.min",
        "jquery.countTo": "assets/plugins/misc/countTo/jquery.countTo",
        "jquery.icheck": "assets/plugins/forms/icheck/jquery.icheck.min",
        "jquery.quicksearch": "assets/plugins/core/quicksearch/jquery.quicksearch",
        "jquery.slimscroll": "assets/plugins/core/slimscroll/jquery.slimscroll.min",
        "jquery.slimscroll.horizontal": "assets/plugins/core/slimscroll/jquery.slimscroll.horizontal.min",
        "jquery.gritter": "assets/plugins/ui/notify/jquery.gritter",
        "jRespond": "assets/js/jRespond.min",
        "pace": "assets/plugins/core/pace/pace.min",
        "respond": "assets/js/libs/respond.min",

        "jquery.tagsinput": "assets/plugins/forms/tags/jquery.tagsinput.min",
        "jquery.select2": "assets/plugins/forms/select2/select2",
        "jquery.select2locale": "assets/plugins/forms/select2/_locale/select2_locale_" + requireConfig.lang,
        "jquery.maskedinput": "assets/plugins/forms/maskedinput/jquery.maskedinput",
        "jquery.datetimepicker": "assets/plugins/forms/datetimepicker/bootstrap-datetimepicker.min",
        "jquery.datetimepickerlocale": "assets/plugins/forms/datetimepicker/locales/bootstrap-datetimepicker." + requireConfig.lang,
        "jquery.datatables": "assets/plugins/tables/datatables/jquery.dataTables.min",
        "jquery.datatablesBS3": "assets/plugins/tables/datatables/jquery.dataTablesBS3",
        "jquery.validate": "assets/plugins/forms/validation/jquery.validate",
        "jquery.redactor": "jquery.redactor/jquery.redactor",
        "jquery.redactorlocale": "jquery.redactor/locale/" + requireConfig.lang,
        "waypoints": "assets/plugins/ui/waypoint/waypoints",
        "jquery.visibility": "jquery.visibility/jquery.visibility",
        "desktop-notify": "desktop-notify"
     },
});