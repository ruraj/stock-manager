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
});

function fillData(data) {
    var json = JSON.parse(data);
    var header = $("#header-row");
    header.empty();

    var body = $("#table-body");
    body.empty();

    header.append("<th>From/To</th>");
    header.append("<th>Item-Code</th>");

    $.each(json['columns'], function(i,v) {
        header.append("<th>"+v['name']+"</th>");
    });
    header.append("<th>Amount</th>");
    header.append("<th>Rate</th>");
    header.append("<th>Date</th>");

    $.each(json.data, function(i,v) {
        var row = "<tr>";
        row += "<td>"+ v.user+"</td>";
        row += "<td>"+ v.itemCode+"</td>";
        $.each(v.item, function(i, v) {
            row += "<td>"+ v.value+"</td>";
        })
        row += "<td>"+ v.amount+"</td>";
        row += "<td>"+ v.rate+"</td>";
        row += "<td>"+ v.datetime+"</td>";

        row += "</tr>";

        body.append(row);
    });

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