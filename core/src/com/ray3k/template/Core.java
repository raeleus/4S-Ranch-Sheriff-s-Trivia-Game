package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.crashinvaders.vfx.VfxManager;
import com.ray3k.template.Trivia.Question;
import com.ray3k.template.screens.*;

public class Core extends JamGame {
    public static final String PROJECT_NAME = "4S Ranch Sheriff's Trivia";
    private static final int MAX_VERTEX_SIZE = 32767;
    public static Core core;
    public SpriteBatch batch;
    public Skin skin;
    public ChangeListener sndChangeListener, sndIncorrectListener, sndCorrectListener;
    public VfxManager vfxManager;
    public CrossPlatformWorker crossPlatformWorker;
    public static enum Binding {
        LEFT, RIGHT, UP, DOWN, SHOOT, SPECIAL, SHIELD;
    }
    public float bgm;
    public float sfx;
    public Preferences preferences;
    public final JsonReader jsonReader = new JsonReader();
    public final ObjectMap<String, Array<String>> categoryToNameList = new ObjectMap<>();
    public final ObjectMap<String, JsonValue> nameToJsonList = new ObjectMap<>();
    public Trivia selectedTrivia;
    public Question selectedQuestion;
    public boolean answerCorrect;
    
    @Override
    public void create() {
        super.create();
        core = this;
        
        preferences = Gdx.app.getPreferences(PROJECT_NAME);
        
        bgm = preferences.getFloat("bgm", 1.0f);
        sfx = preferences.getFloat("sfx", 1.0f);
        
        crossPlatformWorker.create();
        
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        
        batch = new SpriteBatch();
        sndChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                assetManager.get("sfx/click.mp3", Sound.class).play();
            }
        };
        sndIncorrectListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                assetManager.get("sfx/incorrect.mp3", Sound.class).play();
            }
        };
        sndCorrectListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                assetManager.get("sfx/correct.mp3", Sound.class).play();
            }
        };
    
        setScreen(createLoadScreen());
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        
        vfxManager.dispose();
        assetManager.dispose();
        
        super.dispose();
    }
    
    @Override
    public void loadAssets() {
        assetManager.setLoader(Skin.class, new SkinFreeTypeLoader(assetManager.getFileHandleResolver()));
        
        FileHandle fileHandle = Gdx.files.internal("skin.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Skin.class);
        }
    
        fileHandle = Gdx.files.internal("bgm.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Music.class);
        }

        fileHandle = Gdx.files.internal("sfx.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Sound.class);
        }


        fileHandle = Gdx.files.internal("data.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            FileHandle file = Gdx.files.internal(path);
            JsonValue jsonValue = jsonReader.parse(file);
            String category = jsonValue.getString("category");
            if (categoryToNameList.get(category) == null) {
                categoryToNameList.put(category, new Array<>());
            }
            categoryToNameList.get(category).add(file.nameWithoutExtension());
            nameToJsonList.put(file.nameWithoutExtension(), jsonValue);
        }
    }
    
    private Screen createLoadScreen() {
        return new LoadScreen(Actions.run(() -> {
            skin = assetManager.get("skin/skin.json");
            setScreen(createSplashScreen());
        }));
    }
    
    private Screen createSplashScreen() {
        return new SplashScreen(Actions.run(() -> setScreen(createLogoScreen())));
    }
    
    private Screen createLogoScreen() {
        return new LogoScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
    
    private Screen createMenuScreen() {
        return new MenuScreen(Actions.run(() -> setScreen(createRandomizerScreen())),
                Actions.run(() -> setScreen(createRandomizerScreen())),
                Actions.run(() -> setScreen(createSettingsScreen())));
    }
    
    private Screen createRandomizerScreen() {
        return new RandomizerScreen(Actions.run(() -> setScreen(createMenuScreen())),
                Actions.run(() -> setScreen(createQuestionScreen())));
    }
    
    private Screen createQuestionScreen() {
        return new QuestionScreen(Actions.run(() -> setScreen(createMenuScreen())),
                Actions.run(() -> setScreen(createResultScreen())));
    }

    private Screen createResultScreen() {
        return new ResultScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
    
    private Screen createSettingsScreen() {
        return new OptionsScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
    
    public Trivia selectRandomChildrenTrivia() {
        JsonValue root = nameToJsonList.get(categoryToNameList.get("children").random());
        return selectedTrivia = new Trivia(root);
    }
    
    public Trivia selectRandomParentTrivia() {
        JsonValue root = nameToJsonList.get(categoryToNameList.get("parents").random());
        return selectedTrivia = new Trivia(root);
    }
}
