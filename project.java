/*
  Project Name :-- ATM Interface
  Author :- Mayank Joshi
  email :- mayankjoshi5252@gmail.com
  Language Used :- Java
  IDE :- Intelij Idea

 */




import java.sql.*;
import java.util.Scanner;

class customer{
    protected static int pin;
    protected static String user_id;
    boolean validate_user() {
        String s=Integer.toString(pin);
        if((user_id.length()!=8) || (s.length()!=4)){
            return false;
        }
      try {


            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/mj_bank","root","mayankjoshi");
            Statement st=con.createStatement();
            ResultSet rs= st.executeQuery("select user_id,pin from user_detail");
            while (rs.next()){
                String user_id=rs.getString("User_id");
                int pin=rs.getInt("pin");
                if(user_id.equals(this.user_id)){
                    if(pin==this.pin){
                        return true;
                    }
                    else {
                        return false;
                    }
                }

            }


        }
    catch (Exception e){
        System.out.println("Can't Reach Bank Server ,Please Try Again Later..... ");
    }
        return false;
    }


    customer(){}

    customer(String u,int p){
        user_id=u;
        pin=p;
    }

}

class transaction_history extends customer{
    void history(){
 try{
    Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/mj_bank","root","mayankjoshi");
    Statement st=con.createStatement();
    String string="select money,last_op from user_detail where user_id="+customer.user_id+" AND pin= "+customer.pin;
    ResultSet s=st.executeQuery(string);
     System.out.println("\nACCOUNT Detail :- ");
            while (s.next()) {
                String test = s.getString("last_op");
                int m=s.getInt("money");
                System.out.printf("Total Money :- ₹ %d\nLast Operation on Account :- %s",m,test);
            }
 }
 catch (Exception e){
        System.out.println("Can't Reach Bank Server ,Please Try Again Later.....");
    }
    }



}
class deposit extends customer{
    protected int meoney;
    deposit(){}
    deposit(int m){
        meoney=m;
    }
    boolean money_deposit(){
        try {
            int mo=0;
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/mj_bank","root","mayankjoshi");
            Statement st=con.createStatement();
            String dp="select money from user_detail where user_id="+customer.user_id+" AND pin="+customer.pin;
            ResultSet s=st.executeQuery(dp);
            while (s.next()){
                 mo=s.getInt("money");
            }
            mo+=meoney;
            PreparedStatement pst=con.prepareStatement("update user_detail set money=?,last_op=\"Money Deposit BY Cash\" where user_id=?");
            pst.setInt(1,mo);
            pst.setString(2,customer.user_id);
            int n=pst.executeUpdate();
            if(n==1){
                   return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e){
            System.out.println("Can't Reach Bank Server ,Please Try Again Later..... ");
        }
        return false;
    }


}
class withdraw extends customer{
    protected int with_money;
    withdraw(){}
    withdraw(int m){
        with_money=m;

    }
    boolean withdraww(){
        if(with_money>10000 || with_money<0){
            return false;
        }
        try {
            int mo=0;
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/mj_bank","root","mayankjoshi");
            Statement st=con.createStatement();
            String wp="select money from user_detail where user_id="+customer.user_id+" AND pin="+customer.pin;
            ResultSet s=st.executeQuery(wp);
            while (s.next()){
                mo=s.getInt("money");
            }
            if (with_money>mo || with_money <0){
                System.out.println("Money Not Sufficient In The Bank");
                return false;
            }
            else {
                mo-=with_money;
                PreparedStatement pst=con.prepareStatement("update user_detail set money=?,last_op=? where user_id=?");
                pst.setInt(1,mo);
                pst.setString(2,"Money Withdrawn Thru Cash");
                pst.setString(3,customer.user_id);
                int n=pst.executeUpdate();
                if(n==1){
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        catch (Exception e){
            System.out.println("Can't Reach Bank Server ,Please Try Again Later.....");
        }
        return false;
    }


}

class transfer extends customer{
    private int t_money;
    String t_id;
    boolean isvalid(){

        if(t_id.length()!=8){
            return false;
        }
        try {
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/mj_bank","root","mayankjoshi");
            Statement st=con.createStatement();
            ResultSet s=st.executeQuery("Select user_id from user_detail");
            String cho;
            while (s.next()){
                cho=s.getString("user_id");
                if (cho.equals(t_id)){
                    return true;
                }
            }


        }
        catch (Exception e){
            System.out.println("Can't Reach Bank Server ,Please Try Again Later.....");
        }
        return false;
    }
    transfer(){}
    transfer(int m,String t_id){
        t_money=m;
        this.t_id=t_id;
    }
    boolean transfer_money(){
        try {
            int mo=0;
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/mj_bank","root","mayankjoshi");
            Statement st=con.createStatement();
            String wp="select money from user_detail where user_id="+customer.user_id+" AND pin="+customer.pin;
            ResultSet s=st.executeQuery(wp);
            while (s.next()){
                mo=s.getInt("money");
            }
            if(t_money>mo || t_money<0){
                System.out.println("Money Not Sufficient In The Bank");
                return false;
            }
            mo-=t_money;
            String yes="Money Transfer to "+t_id;
            PreparedStatement pst=con.prepareStatement("Update user_detail set money=?,last_op=? where user_id=?");
            pst.setInt(1,mo);
            pst.setString(2,yes);
            pst.setString(3,customer.user_id);
            pst.executeUpdate();
            wp="Select money from user_detail where user_id="+t_id;
            s=st.executeQuery(wp);
            mo=0;
            while (s.next()){
                mo=s.getInt("money");
            }
            mo+=t_money;
            yes="Money Recieved By "+user_id;
            pst=con.prepareStatement("Update user_detail set money=?,last_op=? where user_id=?");
            pst.setInt(1,mo);
            pst.setString(2,yes);
            pst.setString(3,t_id);
           int nq= pst.executeUpdate();
            if(nq==1){
                return true;
            }
            else {
                return false;
            }

    }
        catch (Exception e){
            System.out.println("Can't Reach Bank Server ,Please Try Again Later.....");
        }
        return false;
    }

}
public class project {
    public static void main(String[] args) {
        Scanner ob=new Scanner(System.in);
        System.out.println("                    MJ BANK ATM");
       int pin;
       String user_id;
        System.out.println("\n For Any Query Call MJ BANK ATM Toll Free No :- 1800900358");
        System.out.println("\n\nENTER USER ID :- ");
        user_id=ob.nextLine();
        System.out.println("ENTER PIN :- ");
        pin=ob.nextInt();
        customer object=new customer(user_id,pin);
        if(object.validate_user()){
            System.out.println("            ... WELCOME TO MJ BANK ATM ...       ");

        }
        else {
            System.out.println("WRONG USER ID OR PIN ");
            return;
        }
        System.out.println("Enter Corresponding Number  :");
        System.out.printf("1:-Transaction History       2:- Deposit\n3:-Withdrawl                 4:-Transfer\n5:-Quit\n");
        int choice=ob.nextInt();
        switch (choice){
            case 1:
                transaction_history t_h=new transaction_history();
                t_h.history();
                break;
            case 2:
                System.out.println("\nENTER AMOUNT YOU WANT TO DEPOSIT LESS THAN 12000 AT SINGLE TIME:- ");
                int money_to_be_deposited=ob.nextInt();
                deposit d=new deposit(money_to_be_deposited);
                if(d.money_deposit()){
                    System.out.println(money_to_be_deposited+" Sucessfully deposited in the bank");
                }
                else {
                    System.out.println("Can't Reach Bank Server ,Please Try Again Later.....\n");
                }
                break;

            case 3:
                System.out.println("\nENTER AMOUNT YOU WANT TO WITHDRAW LESS THAN 10000 AT ONE TIME :-");
                int money_to_be_withdrawn=ob.nextInt();
                withdraw w=new withdraw(money_to_be_withdrawn);
                if (w.withdraww()){
                    System.out.println(money_to_be_withdrawn+" Sucessfully Withdrwan");
                }
                else {
                    System.out.println("Can't Reach Bank Server ,Please Try Again Later.....\n");
                }
                break;
            case 4:

                System.out.println("PLEASE ENTER RECIEVER USER ID:- ");
                ob.nextLine();
                String reciever_user_id=ob.nextLine();

                System.out.println("\nENTER AMOUNT TO TRANSFER :-");
                int money_to_reciever=ob.nextInt();
                transfer o=new transfer(money_to_reciever,reciever_user_id);
                if(!(o.isvalid())){
                    System.out.println("The User Do Not Exist,Enter Valid Id ...");
                    break;
                }
                if(o.transfer_money()){
                    System.out.println(money_to_reciever+" TO USER "+reciever_user_id+" Sucessfull");
                }
                else {
                    System.out.println("\nCan't Reach Bank Server ,Please Try Again Later.....");
                }

                break;

            case 5:
                System.out.println("\nTHANK YOU FOR USING MJ BANK ATM SERVICE\n");
                System.out.println("HAVE A GOOD DAY !!!\n");
                return;
        }

    }
}
