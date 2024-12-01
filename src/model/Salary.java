package model;

import java.util.Date;

public class Salary {
    private int salaryId;
    private int employeeId;
    private Date paymentDate;
    private double paymentAmount;
    private String paymentType;

    public Salary(int salaryId, int employeeId, Date paymentDate, double paymentAmount, String paymentType) {
        this.salaryId = salaryId;
        this.employeeId = employeeId;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.paymentType = paymentType;
    }

    // Getters and setters
    public int getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(int salaryId) {
        this.salaryId = salaryId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
