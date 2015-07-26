package com.smart.doorlock.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class PwdEntity extends SugarRecord<PwdEntity> implements Serializable {

    private String title;
    private String password;
    private int permission;
    private long time;
    private String author;

    public PwdEntity(String author, String password, String title) {
        this.author = author;
        this.password = password;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
