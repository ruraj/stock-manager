$(".chosen").chosen( {
    no_results_text: "User not found"
});

$("#commit-user").click () ->
    $.post "/dealer/addUser", $("#user-add-form").serialize(), (users) ->
        $.each users, (user) ->
            $(".group-users-list").append '<li class="user"><strong><a href="/users/'+user.id+'">'+user.fullname+'</a></strong><span class="pull-right light">'+user.email+'</span></li>'
