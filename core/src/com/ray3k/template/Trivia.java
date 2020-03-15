package com.ray3k.template;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class Trivia {
    public String title;
    public int index;
    public String category;
    public Array<Question> questions;
    
    public Trivia(JsonValue root) {
        title = root.getString("title");
        index = root.getInt("index");
        category = root.getString("category");
        
        questions = new Array<>();
        for (JsonValue jsonValue : root.get("questions").iterator()) {
            Question question = new Question();
            questions.add(question);
            
            question.question = jsonValue.getString("question");
            
            for (JsonValue jsonValue1 : jsonValue.iterator()) {
                if (jsonValue1.name.equals("correct")) {
                    question.correct.add(jsonValue1.asString());
                    question.answers.add(jsonValue1.asString());
                } else if (jsonValue1.name.equals("incorrect")) {
                    question.incorrect.add(jsonValue1.asString());
                    question.answers.add(jsonValue1.asString());
                } else if (jsonValue1.name.equals("result")) {
                    question.result = jsonValue1.asString();
                } else if (jsonValue1.name.equals("result-image")) {
                    question.resultImage = jsonValue1.asString();
                }
            }
            
            if (jsonValue.getBoolean("randomize", true)) {
                question.answers.shuffle();
            }
        }
    }
    
    public static class Question {
        public String question;
        public Array<String> answers = new Array<>();
        public Array<String> correct = new Array<>();
        public Array<String> incorrect = new Array<>();
        public String result;
        public String resultImage;
    }
}
