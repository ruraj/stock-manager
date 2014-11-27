/**
 * Created by ruraj on 10/6/14.
 */

var dataTable;

var substringMatcher = function(strs) {
    return function findMatches(q, cb) {
        var matches, substrRegex;
        matches = [];
        substrRegex = new RegExp(q, 'i');
        $.each(strs, function(i, str) {
            if (substrRegex.test(str.code)) {
                matches.push(str);
            }
        });
        cb(matches);
    };
};
function copyRow(type) {
    dataTable.DataTable().destroy();

    var amnt = type * $('#amount').val();
    var rate = amnt * $('#rate').val();
    var button = $("<button class='btn btn-xs btn-danger delete-button' type='button'><i class='fa fa-trash-o'></i></button>");
    $("#table-body")
        .append("<tr>").find("tr:last")
        .append("<td>").find("td:last")
        .append($('#item-code').clone().attr("name", sampleCode).attr("readonly", "readonly"))
        .parent()
        .append("<td>").find("td:last")
        .append($('#amount').clone().attr("name", sampleAmount).val(amnt).attr("readonly", "readonly"))
        .parent()
        .append("<td>").find("td:last")
        .append($('#rate').clone().attr("name", sampleRate).attr("readonly", "readonly"))
        .parent()
        .append("<td>").find("td:last")
        .append($('<input id="#total" class="total form-control" type="number" step="any" disabled>').val(rate))
        .parent()
        .append("<td>").find("td:last")
        .append($('#date').clone().attr("name", sampleDate).attr("readonly", "readonly"))
        .parent()
        .append("<td>").find("td:last")
        .append(button);

    button.on("click", function() {
        dataTable.DataTable().destroy();
        $(this).parent().parent().remove();
        redraw();
        renumberDataMap();
        calcTotal();
    });

    renumberDataMap();

    redraw();
}
function calcTotal() {
    var total = 0;
    $.each($("#table-body").find(".total"), function() {
        total += parseInt($(this).val());
    });

    console.log(total);
    $("#total-amount").text("Rs "+total)
}
var renumberDataMap = function() {
    Array.prototype.reduce.call($('#table-body').find('tr'), function (i, next) {
//            var label = $(next).children('label');
//            label.attr('for', label.attr('for').replace(/_\d+__/, '_' + i + '__'));
        var input = $(next).find('input');
//            input.attr('id', input.attr('id').replace(/_\d+__/, '_' + i + '__'));
        $.each(input, function() {
            if (!$(this).prop('disabled')) {
                $(this).attr('name', $(this).attr('name').replace(/\[\d+\]/, '[' + i + ']'));
            }
        })
//            if (input.attr('id').match(/transaction-list_\d+__value/)) {
        return i + 1;
//            }
//            return i;
    }, 0);
};

function redraw() {
    dataTable.DataTable({
        "dom": 'T<"clear">C<"clear">Rlfrtip',
        "tableTools": {
            "sSwfPath": "/swf/copy_csv_xls_pdf.swf"
        },
        destroy: true,
        ordering: true,
        orderMulti: true,
        responsive: true
    });

    calcTotal();
}
$(document).ready(function() {
    dataTable = $("#transaction-table");

    dataTable.on('draw.dt', function() {
        calcTotal();
    });

    $('#item-code').typeahead({
            hint: true,
            highlight: true,
            minLength: 1
        },
        {
            name: 'items',
            displayKey: 'code',
            source: substringMatcher(list),
            templates: {
                empty: [
                    '<div class="empty-message">',
                    'no such item',
                    '</div>'
                ].join('\n'),
                suggestion: Handlebars.compile('<p><strong>{{code}}</strong><br/>{{values}}</p>')
            }
        });
    $("#add-button").click(function () {
        copyRow(-1);
    });
    $("#sell-button").click(function () {
        copyRow(1);
    });

    $("#clear-button").click(function () {
        dataTable.DataTable().destroy();
        $("#table-body").find("*").remove();
        calcTotal();
    });
});


