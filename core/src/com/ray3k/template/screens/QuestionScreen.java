package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;
import com.ray3k.template.Trivia.Question;

public class QuestionScreen extends JamScreen {
    private Action menuAction;
    private Action resultAction;
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    
    public QuestionScreen(Action menuAction, Action resultAction) {
        this.menuAction = menuAction;
        this.resultAction = resultAction;
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
        
        core.selectedQuestion = core.selectedTrivia.questions.random();
    
        final Music bgm = core.assetManager.get("bgm/music-test.mp3");
        if (!bgm.isPlaying()) {
            bgm.play();
            bgm.setVolume(core.bgm);
            bgm.setLooping(true);
        }
        
        stage = new Stage(new ScreenViewport(), core.batch);
        Gdx.input.setInputProcessor(stage);
    
        Table root = new Table();
        root.setBackground(skin.getDrawable("background-ten"));
        root.setFillParent(true);
        stage.addActor(root);
    
        final Image fg = new Image(skin, "white");
        fg.setColor(Color.BLACK);
        fg.setFillParent(true);
        fg.setTouchable(Touchable.disabled);
        stage.addActor(fg);
        fg.addAction(Actions.sequence(Actions.fadeOut(.3f)));
        
        Button button = new Button(skin, "close");
        root.add(button).expandX().right().top().pad(15f).uniformY();
        button.addListener(core.sndChangeListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), menuAction));
            }
        });
        
        root.row();
        Table table = new Table();
        table.setBackground(skin.getDrawable("panel-dark-ten"));
        root.add(table).grow().pad(25f);
        
        table.pad(20);
        table.defaults().space(10);
        
        Label label = new Label(core.selectedTrivia.title, skin, "underline");
        table.add(label);
        
        table.row();
        label = new Label(core.selectedQuestion.question, skin);
        label.setAlignment(Align.center);
        label.setWrap(true);
        table.add(label).growX();
        
        ChangeListener resultListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fg.addAction(Actions.sequence(Actions.delay(.5f), Actions.fadeIn(.3f), resultAction));
            }
        };
        
        table.row();
        Table subTable = new Table();
        table.add(subTable);
        
        subTable.defaults().uniformX().fillX().space(20).minWidth(200);
        if (core.selectedQuestion.answers.size > 0) {
            for (String answer : core.selectedQuestion.answers) {
                subTable.row();
                TextButton textButton = new TextButton(answer, skin);
                textButton.setOrigin(Align.center);
                subTable.add(textButton);
        
                if (core.selectedQuestion.correct.contains(answer, false)) {
                    textButton.addListener(core.sndCorrectListener);
                    textButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            core.answerCorrect = true;
                        }
                    });
                } else {
                    textButton.addListener(core.sndIncorrectListener);
                    textButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            core.answerCorrect = false;
                        }
                    });
                }
                textButton.addListener(resultListener);
            }
        } else {
            subTable.row();
            TextButton textButton = new TextButton("Continue", skin);
            subTable.add(textButton);
            textButton.addListener(core.sndChangeListener);
            textButton.addListener(resultListener);
        }
        
        root.row();
        root.add().uniformY();
    }
    
    @Override
    public void act(float delta) {
        stage.act(delta);
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
