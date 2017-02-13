 package com.ge.apm.view.analysis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

public class Row {

        private String name;
        private String serial_num;
        private String clinical_dept_name;
        private String date;
        private double revenue;
        private long scan;
        private double expo;
        private double cost;
        private double profit;
        private long repair;
        private double dt;

        private static final NumberFormat cf = new DecimalFormat(",###.##");
        private static final NumberFormat cfint = new DecimalFormat(",###");

        public Row (String name, String serial_num, String clinical_dept_name, double revenue, long scan,
                    double expo, double cost, double profit, long repair, double dt) {

            this.name = name;
            this.serial_num = serial_num;
            this.clinical_dept_name = clinical_dept_name;
            this.revenue = revenue;
            this.scan = scan;
            this.expo = expo;
            this.cost = cost;
            this.profit = profit;
            this.repair = repair;
            this.dt = dt;
        }

        public Row (String name, String serial_num, String clinical_dept_name, String date, double revenue, long scan,
                    double expo, double cost, double profit, long repair, double dt) {

            this.name = name;
            this.serial_num = serial_num;
            this.clinical_dept_name = clinical_dept_name;
            this.date = date;
            this.revenue = revenue;
            this.scan = scan;
            this.expo = expo;
            this.cost = cost;
            this.profit = profit;
            this.repair = repair;
            this.dt = dt;
        }


        public Row (Map<String, Object> item) {

            revenue = item.get("revenue")!=null ? (double)item.get("revenue") : 0.0;
            scan = item.get("scan")!=null ? (long)item.get("scan") : 0;
            expo = item.get("expo")!=null ? (double)item.get("expo") : 0.0;

        }

        public Row (Map<String, Object> item, double depre) {

            cost = depre + (item.get("price")!=null ? (double)item.get("price") : 0.0);
            repair = item.get("repair")!=null ? (long)item.get("repair") : 0;
            dt = item.get("dt")!=null ? (double)item.get("dt") : 0.0;

        }


        public Row (String name, String serial_num, String clinical_dept_name, String key, Row row_1, Row row_2, double depre) {

            this.date = key;
            this.name = name;
            this.serial_num = serial_num;
            this.clinical_dept_name = clinical_dept_name;

            if (row_1 != null) {
                revenue = row_1.revenue;
                scan = row_1.scan;
                expo = row_1.expo;
            } 
            else {
                revenue = 0.0;
                scan = 0;
                expo = 0.0;
            }

            if (row_2 != null) {
                cost = row_2.cost;
                repair = row_2.repair;
                dt = row_2.dt;
            }
            else {
                cost = depre;
                repair = 0;
                dt = 0.0;
            }

            profit = revenue - cost;

        }


        public String getName() {

            return name;
        }

        public String getSerial_num() {

            return serial_num;
        }

        public String getclinical_dept_name() {

            return clinical_dept_name;
        }

        public String getDate() {

            return date;
        }

        public String getRevenue() {

            return cf.format(revenue);
        }

        public String getScan() {

            return cf.format(scan);
        }

        public String getExpo() {

            return cfint.format(expo);
        }

        public String getCost() {

            return cf.format(cost);
        }

        public String getProfit() {

            return cf.format(profit);
        }

        public String getRepair() {

            return cf.format(repair);
        }

        public String getDt() {

            return cf.format(dt);
        }

        public void toPrint(){

            String output = String.format("name = %s, date = %s, revenue = %s, scan = %s, expo = %s, cost = %s, profit = %s, repair = %s, dt = %s",
                getName(), getDate(), getRevenue(), getScan(), getExpo(), getCost() ,getProfit(), getRepair(), getDt());
            System.out.println(output);
        }

    }