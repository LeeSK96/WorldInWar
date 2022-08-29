package com.koreait.project_war;

public class InvenUnitVO {
    private int inven_idx;
    private int user_idx;
    private int unit_idx;
    private int unit_amount;
    private int unit_position;
    private int unit_state;

    private int unit_attack,unit_defence,unit_price,can_attack,can_defence;
    private String unit_name,unit_img;

    public int getUser_idx() {
        return user_idx;
    }

    public int getInven_idx() {
        return inven_idx;
    }

    public void setInven_idx(int inven_idx) {
        this.inven_idx = inven_idx;
    }

    public int getUnit_attack() {
        return unit_attack;
    }

    public void setUnit_attack(int unit_attack) {
        this.unit_attack = unit_attack;
    }

    public int getUnit_defence() {
        return unit_defence;
    }

    public void setUnit_defence(int unit_defence) {
        this.unit_defence = unit_defence;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public int getCan_attack() {
        return can_attack;
    }

    public void setCan_attack(int can_attack) {
        this.can_attack = can_attack;
    }

    public int getCan_defence() {
        return can_defence;
    }

    public void setCan_defence(int can_defence) {
        this.can_defence = can_defence;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getUnit_img() {
        return unit_img;
    }

    public void setUnit_img(String unit_img) {
        this.unit_img = unit_img;
    }

    public void setUser_idx(int user_idx) {
        this.user_idx = user_idx;
    }

    public int getUnit_idx() {
        return unit_idx;
    }

    public void setUnit_idx(int unit_idx) {
        this.unit_idx = unit_idx;
    }

    public int getUnit_amount() {
        return unit_amount;
    }

    public void setUnit_amount(int unit_amount) {
        this.unit_amount = unit_amount;
    }

    public int getUnit_position() {
        return unit_position;
    }

    public void setUnit_position(int unit_position) {
        this.unit_position = unit_position;
    }

    public int getUnit_state() {
        return unit_state;
    }

    public void setUnit_state(int unit_state) {
        this.unit_state = unit_state;
    }
}
