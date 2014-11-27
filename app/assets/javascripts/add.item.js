var derivedOnes;

$(document).ready(function() {
    derivedOnes = $(".derived")

    $("select").dblclick(function () {
        var selected = $(this);
        var option = selected.find('option:selected');
        var to = option.attr("data-to");
        var code = option.attr("data-code");
        var value = option.attr("data-value");
//        console.log(option.attr("name") +"->"+code);
        $("input[name='" + option.attr("name") + "']").val(code);
//        console.log(option.attr("data-code"))
//        console.log(to + "->" + value);
        $("input[name='" + to + "']").val(value);

        calc()
    });

    $("#my-form").ajaxForm({
        success: function() {
            mNotify("Success", "info");
        },
        error: function() {
            mNotify("Error", "error");
        }
    })
})

function calc() {
    console.log("calculating "+derivedOnes.size())
    $.each(derivedOnes, function() {
        console.log("D: "+$(this).attr("data-formula"))
    })
}

var renumberDataMap = function() {
    Array.prototype.reduce.call($('.my-field'), function (i, next) {
//            var label = $(next).children('label');
//            label.attr('for', label.attr('for').replace(/_\d+__/, '_' + i + '__'));
        var input = $(next).find('input');
        var option = $(next).find('option');
//            input.attr('id', input.attr('id').replace(/_\d+__/, '_' + i + '__'));
        $.each(input, function() {
            $(this).attr('name', $(this).attr('name').replace(/\[\d+\]/, '[' + i + ']'));
        })
        $.each(option, function() {
            $(this).attr('data-to', $(this).attr('data-to').replace(/\[\d+\]/, '[' + i + ']'));
            $(this).attr('name', $(this).attr('name').replace(/\[\d+\]/, '[' + i + ']'));
        })
//            if (input.attr('id').match(/transaction-list_\d+__value/)) {
        return i + 1;
//            }
//            return i;
    }, 0);
}