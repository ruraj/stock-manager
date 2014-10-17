$(document).ready(function() {

    body = $("#derivation-body")

    $("#value-type").change(function() {
        if (this.value == "derived") {
            $("#derivation-box").css("display", "auto");
        } else {
            $("#derivation-box").css("display", "none");
        }
    });

    $(".expr").click(function() {
        cloned = $(this).clone()
        appendo(cloned);
    });

    $("#numeric-button").click(function() {
        newNumber = $("<div class='btn btn-default col-xs-2 col-sm-2 col-md-2 col-lg-2'>"+$("#numeric-value").val()+"</div>")
        appendo(newNumber)
    })

    $("#clear-button").click(function() {
        body.clear()
    })

    function appendo(object) {
        body.append(object)
        object.attr("onclick", "$(this).remove()")
        input = $("#derivation")
        console.log(input.val())
        console.log(object.text())
        input.val(input.val()+object.text())
    }
//    $(".removable").click(function() {
//        $(this).remove()
//    })
})
