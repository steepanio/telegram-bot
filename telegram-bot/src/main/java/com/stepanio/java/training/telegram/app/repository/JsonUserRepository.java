package com.stepanio.java.training.telegram.app.repository;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;

import java.io.File;
import java.util.Optional;

public class JsonUserRepository implements UserRepository{
    private File dir = new File("users");

    public JsonUserRepository() {
        if (!this.dir.exists()){
            this.dir.mkdirs();
        }
    }

    @Override
    public Optional<BotUserDto> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean exists(Long id) {
        String fileName = "user_" + id + ".json";
        File fileUser = new File(dir, fileName);
        return fileUser.exists();
    }

    @Override
    public void save(BotUserDto botUserDto) {

    }

    @Override
    public void delete(Long id) {

    }
}
