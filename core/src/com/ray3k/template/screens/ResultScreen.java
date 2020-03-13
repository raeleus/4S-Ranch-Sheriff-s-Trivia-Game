package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class ResultScreen extends JamScreen {
    private Action menuAction;
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    
    public ResultScreen(Action menuAction) {
        this.menuAction = menuAction;
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
        
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
        root.add(button).expand().right().top().pad(15f);
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
        root.add(table).grow();
        
        table.defaults().space(20);
        if (core.answerCorrect) {
            Image image = new Image(skin, "correct");
            table.add(image);
        } else {
            Image image = new Image(skin, "incorrect");
            table.add(image);
        }
        
        table.row();
        Label label = new Label(core.selectedQuestion.question, skin, "underline");
        label.setAlignment(Align.center);
        label.setWrap(true);
        table.add(label).growX();
        
        table.row();
        label = new Label(core.selectedQuestion.result, skin);
        label.setAlignment(Align.center);
        label.setWrap(true);
        table.add(label).growX();
        
        table.row();
        TextButton textButton = new TextButton("Return to Menu", skin);
        table.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), menuAction));
            }
        });
        
        root.row();
        root.add().expand();
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
