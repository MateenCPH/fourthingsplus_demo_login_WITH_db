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

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("addtask", ctx -> addtask(ctx, connectionPool));
        app.post("done", ctx -> done(ctx, true, connectionPool));
        app.post("undo", ctx -> done(ctx, false, connectionPool));
        app.post("delete", ctx -> delete(ctx, connectionPool));
        app.post("edit", ctx -> edit(ctx, connectionPool));
        app.post("update", ctx -> update(ctx, connectionPool));
    }

    private static void update(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");

        try {
            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            String taskName = ctx.formParam("taskname");
            TaskMapper.update(taskId, taskName, connectionPool);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.redirect("index.html");
        }
    }

    private static void edit(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");

        try {
            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            Task task = TaskMapper.getTaskById(taskId, connectionPool);
            ctx.attribute("task", task);
            ctx.render("edittask.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.redirect("index.html");
        }
    }

    private static void delete(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");

        try {
            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            TaskMapper.delete(taskId, connectionPool);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.redirect("index.html");
        }
    }

    private static void done(Context ctx, boolean done, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");

        try {
            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            TaskMapper.setDoneTo(done, taskId, connectionPool);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.redirect("index.html");
        }
    }

    private static void addtask(Context ctx, ConnectionPool connectionPool) {
        String taskname = ctx.formParam("taskname");
        User user = ctx.sessionAttribute("currentUser");

        try {
            if (taskname.length() > 3) {
                Task newTask = TaskMapper.addTask(user, taskname, connectionPool);
            } else {
                ctx.attribute("message", "En task skal være længere end 3 tegn!");
            }
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(), connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render("task.html");

        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt. Prøv evt. igen");
            ctx.render("task.html");
        }
    }
}

