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
})

function calc() {
    console.log("calculating "+derivedOnes.size())
    $.each(derivedOnes, function() {
        console.log("D: "+$(this).attr("data-formula"))
    })
}