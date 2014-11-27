/**
 * Created by ruraj on 10/13/14.
 */
var table;

$(document).ready(function() {
    $("#form").ajaxForm({
        beforeSubmit: function(arr, $form, options) {
            mNotify("Loading", "info");
        },
        success: function(data) {
            mNotify("Fetched Data", "info");
            fillData(data);
        },
        error: function() {
            mNotify("Error fetching data", "error");
        }
    });

    table = $("#data-table");

    table.on('draw.dt', function() {
       calcTotal();
    });
});

function fillData(data) {

    var json = JSON.parse(data);
    var header = $("#header-row");
    header.empty();

    var body = $("#table-body");
    body.empty();

    var foot = $("#footer-row");
    foot.empty();

    var totalRow = "";
    $.each(json['columns'], function(i,v) {
        header.append("<th>"+v['name']+"</th>");
        if ((i+1) == json.columns.size) {
            totalRow += "<td>Total</td>";
        } else {
            totalRow += "<td></td>"
        }
    });
    header.append("<th>Amount</th>");

    var totalAmount = 0;
    $.each(json.data, function(i,v) {
        var row = "<tr>";
        $.each(v.item, function(i, v) {
            row += "<td>"+ v.value+"</td>";
        })
        row += "<td class='amount'>"+ v.amount+"</td>";

        row += "</tr>";

        body.append(row);

        totalAmount += v.amount;
    });

    foot.append(totalRow+"<td id='total'>"+totalAmount+"</td>");

    table.DataTable({
        "dom": 'T<"clear">C<"clear">Rlfrtip',
        "tableTools": {
            "sSwfPath": "/swf/copy_csv_xls_pdf.swf"
        },
        destroy: true,
        lengthChange: true,
        ordering: true,
        orderMulti: true,
        processing: true,
        responsive: true
    });
}

function calcTotal() {
    var total = 0;
    $.each($("#table-body").find(".amount"), function() {
        total += parseInt($(this).text());
    });

    $("#total").text(total)
}