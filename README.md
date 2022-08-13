# IIT_Bank_System

####   **Project Intro**
This is a final project of MD510. It simulates the following processes:
Login as Client, Manager or Admin role, redirecting to another page, JDBC connecting and CRUD operations, rendering a page.



####  **Software Architecture**
Software architecture specs:

1. Jdk 11
2. javafx 16（Actual test 17 can also remain compatible）




####  **Installation Guide**

1.  JDK Download:https://www.injdk.cn/
2.  JavaFX Download:https://gluonhq.com/products/javafx/



#### configuration

1. The hard-coded way in the project has a built-in mysql link address, and the table and admin and manager accounts are automatically created the first time the program is started. You can change this section at cn.someget.Dao.DBConnect.

2. admin and manager initial passwords are 123456,You can change this section at cn.someget.Dao.RecordHelper#setupInitRole.
3. At present, the project involves two tables, one is the user storage user record table, one is to store all user tables, all table names are stored uniformly, you can modify cn.someget.utils.Constants here

#### Project structure

```xml
├── java
│   └── cn
│       └── someget
│           ├── Dao // Communicate with db
│           ├── application // App launch entry
│           ├── controllers // The presentation of the view
│           ├── models // Objects in the business
│           └── utils // Other tools
└── resources
    ├── imgs // Static images
    └── views javaFX's view file
```



#### **Role**

| Role    | Desc                         | Permissions                                                  |
| ------- | ---------------------------- | ------------------------------------------------------------ |
| Client  | Ordinary readers             | - Library<br />- Return                                      |
| Admin   | System Supreme Administrator | - Open an account (including managers)<br />- Modify accounts (including managers)<br />- Delete an account (including managers)<br />-View all accounts (including managers)<br />-View an account's borrowing and return history (including managers) |
| Manager | Library Manager              | - Open an account (Just the reader)<br />- Modify accounts (Just the reader)<br />- Delete an account (Just the reader)<br />-View all accounts (Just the reader)<br />-View an account's borrowing and return history (Just the reader) |

####  **Features**

- Landing page

<img src="https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172142.png" alt="image-20220813172142559" style="zoom:50%;" />

- Reader page presentation

![image-20220813172602324](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172602.png)

- Readers borrow books

![image-20220813172623523](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172623.png)

- Readers return the book

![image-20220813172639843](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172639.png)

- Readers view their own records

![image-20220813172648258](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172648.png)

- Admin interface display

![image-20220813172834149](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172834.png)

- admin Manages users

![image-20220813172849429](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172849.png)

- Managers manage users

  ![](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172919.png)

- Some critical value anomalies

![image-20220813172952833](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813172952.png)

![image-20220813173107578](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813173107.png)

![image-20220813173154728](https://mypicgogo.oss-cn-hangzhou.aliyuncs.com/tuchuang20220813173154.png)



####  **Contributors**

1.  Yifan Zou




#### Special Techniques

1.  Hash encryption algorithm for password insertion.
2.  Prepared statement Prevent SQL injection
3.  Good-looking UI, Follow Google's MD