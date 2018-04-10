package com.example.dibage.accountb.entitys;

import android.util.Log;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class Photo {
    //ID主键自增

    public Photo(String photo_path, Long cardId) {
        this.photo_path = photo_path;
        this.cardId = cardId;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", photo_path='" + photo_path + '\'' +
                ", cardId=" + cardId +
                '}';
    }

    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String photo_path;//证件照片路径
    @NotNull
    private Long cardId;//设计此字段，在Card中引用，以便于Card来识别它
    @Generated(hash = 1612885589)



    public Photo(Long id, @NotNull String photo_path, @NotNull Long cardId) {
        this.id = id;
        this.photo_path = photo_path;
        this.cardId = cardId;
    }
    @Generated(hash = 1043664727)
    public Photo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPhoto_path() {
        return this.photo_path;
    }
    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }
    public Long getCardId() {
        return this.cardId;
    }
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
