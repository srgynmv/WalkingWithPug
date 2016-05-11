package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public class GoalManager {
    public enum Type {
        ONLY_PEE, GO_TO_GRANDMA
    }
    private static class Goal {
        String goalText;
        String winText;
        String loseText;
        Type type;
    }

    private Goal goal;

    public GoalManager(int dayNumber) {
        goal = new Goal();
        ArrayList<Goal> goalList = loadGoalList("goals.json");
        System.out.println(dayNumber);
        goal = goalList.get((dayNumber - 1) % goalList.size());
    }

    private ArrayList<Goal> loadGoalList(String path) {
        Json json = new Json();
        ArrayList<Goal> goalList = new ArrayList<Goal>();
        ArrayList<JsonValue> list = json.fromJson(
                ArrayList.class,
                Gdx.files.internal(path)
        );
        for (JsonValue v : list) {
            goalList.add(json.readValue(Goal.class, v));
        }
        return goalList;
    }

    public String getGoalText() {
        return goal.goalText;
    }

    public String getWinText() {
        return goal.winText;
    }

    public String getLoseText() {
        return goal.loseText;
    }

    public Type getGoalType() {
        return goal.type;
    }
}
