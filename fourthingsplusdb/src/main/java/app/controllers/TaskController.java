package app.controllers;

import app.entities.Task;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.TaskMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class TaskController {
<<<<<<< Updated upstream

    public static void addRoutes(Javalin app, ConnectionPool connectionPool){
        app.post("addtask", ctx -> addtask(ctx, connectionPool));
    }

    private static void addtask(Context ctx, ConnectionPool connectionPool) {
        String taskname = ctx.formParam("taskname");
        if (taskname.length() > 3){
            User user = ctx.sessionAttribute("currentUser");
            try {
                Task newTask = TaskMapper.addTask(user, taskname, connectionPool);
                List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
                ctx.attribute("taskList", taskList);
                ctx.render("task.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Noget gik galt. Prøv evt. igen");
                ctx.render("task.html");
            }
=======
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/createtask", ctx -> createTask(ctx, connectionPool));
        app.post("/done", ctx -> done(ctx, connectionPool));
        app.post("/undo", ctx -> undo(ctx, connectionPool));
        app.post("/edittask", ctx -> edit(ctx, connectionPool));
        app.post("/updatetask", ctx -> update(ctx, connectionPool));
    }

    private static void update(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");


        try {

            int taskId = Integer.parseInt(ctx.attribute("taskId"));
            String taskName = ctx.formParam("taskname");
            TaskMapper.update(taskId, taskName, connectionPool);

            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");

        } catch (DatabaseException e) {

            ctx.attribute("message", e.getMessage());
            ctx.render("task.html");
        }
    }

    private static void edit(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");


        try {

            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            Task task = TaskMapper.getTaskById(taskId, connectionPool);
            ctx.attribute("task",task);

        } catch (DatabaseException | NumberFormatException e) {

            ctx.attribute("message", e.getMessage());
            ctx.render("task.html");
        }
    }

    private static void undo(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        try {

            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            TaskMapper.setDoneTo(false, taskId, connectionPool);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");

        } catch (DatabaseException | NumberFormatException e){
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }
    }

    private static void done(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        try {

        int taskId = Integer.parseInt(ctx.formParam("taskId"));
            TaskMapper.setDoneTo(true, taskId, connectionPool);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");

        } catch (DatabaseException | NumberFormatException e){
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }

    }

    private static void createTask(Context ctx, ConnectionPool connectionPool) {
        String taskName = ctx.formParam("taskname");
        User user = ctx.sessionAttribute("currentUser");

        if(taskName.length() > 3){


            try {

                Task newTask = TaskMapper.addTask(user,taskName, connectionPool);
                List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
                ctx.attribute("taskList", taskList);
                ctx.render("task.html");

            } catch (DatabaseException e) {

                ctx.attribute("message", "Something went wrong, try again...");
                ctx.render("task.html");
            }
        } else {
            ctx.attribute("message", "Task-name is too short. Has to be 3 or more characters. Try again...");
            List<Task> taskList = null;
            try {
                taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
            }
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");
>>>>>>> Stashed changes
        }
    }
}
