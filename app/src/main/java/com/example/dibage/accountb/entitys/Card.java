package com.example.dibage.accountb.entitys;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.dao.PhotoDao;
import com.example.dibage.accountb.dao.CardDao;

//实体类：证件 Card
@Entity
public class Card {
    //ID主键自增
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String card_name;//证件名称
    @NotNull
    private String username;//证件人姓名
    @NotNull
    private String card_number;//卡号
    private String remark;//备注
    //cardId为photo类中定义的一个属性
    @ToMany(referencedJoinProperty = "cardId")
    private List<Photo> photoList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 599084715)
    private transient CardDao myDao;
    @Generated(hash = 619673)
    public Card(Long id, @NotNull String card_name, @NotNull String username,
            @NotNull String card_number, String remark) {
        this.id = id;
        this.card_name = card_name;
        this.username = username;
        this.card_number = card_number;
        this.remark = remark;
    }

    public Card(String card_name, String username, String card_number, String remark) {
        this.card_name = card_name;
        this.username = username;
        this.card_number = card_number;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", card_name='" + card_name + '\'' +
                ", username='" + username + '\'' +
                ", card_number='" + card_number + '\'' +
                ", remark='" + remark + '\'' +
                ", photoList=" + photoList +
                '}';
    }

    @Generated(hash = 52700939)
    public Card() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCard_name() {
        return this.card_name;
    }
    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getCard_number() {
        return this.card_number;
    }
    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1161981795)
    public List<Photo> getPhotoList() {
        if (photoList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PhotoDao targetDao = daoSession.getPhotoDao();
            List<Photo> photoListNew = targetDao._queryCard_PhotoList(id);
            synchronized (this) {
                if (photoList == null) {
                    photoList = photoListNew;
                }
            }
        }
        return photoList;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1816394616)
    public synchronized void resetPhotoList() {
        photoList = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1693529984)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCardDao() : null;
    }

}
