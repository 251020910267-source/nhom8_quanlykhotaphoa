package com.mycompany.quanlydoituongdacbiet.action;

import java.util.List;

public interface IQuanLy<T> {
    void add(T obj);
    void edit(T obj);
    boolean delete(T obj);
    List<T> getList();
}