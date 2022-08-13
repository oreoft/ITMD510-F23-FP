package cn.someget.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooksRecordModel {
    private int cid;
    private int tid;
    private double balance;
    private String balanceStr;
}
