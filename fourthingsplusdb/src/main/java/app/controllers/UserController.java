package app.controllers;

import app.entities.Task;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.TaskMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool){
        app.post("/login", ctx -> login(ctx, connectionPool));
    }

    public static void login(Context ctx, ConnectionPool connectionPool){
        //Hent form parametre
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");


        //Check om bruger findes i DB med de angivne username + password
        try {

            User user = UserMapper.login(username, password, connectionPool);
            ctx.sessionAttribute("currentUser",user);
            //Hvis ja, Send videre til task siden
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");

        } catch (DatabaseException e) {

            //Hvis nej, send tilbage til loginside med fejlbesked
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }


    }
}