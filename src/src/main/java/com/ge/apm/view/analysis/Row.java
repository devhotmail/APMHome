 package com.ge.apm.view.analysis;

 
   public class Row {

        private String name;
        private String serial_num;
        private String clinical_dept_name;
        private String revenue;
        private String scan;
        private String expo;
        private String cost;
        private String profit;
        private String repair;
        private String dt;


        public Row (String name, String serial_num, String clinical_dept_name, String revenue, String scan,
                    String expo, String cost, String profit, String repair, String dt) {

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

        public String getName() {

            return name;
        }

        public String getSerial_num() {

            return serial_num;
        }

        public String getclinical_dept_name() {

            return clinical_dept_name;
        }

        public String getRevenue() {

            return revenue;
        }

        public String getScan() {

            return scan;
        }

        public String getExpo() {

            return expo;
        }

        public String getCost() {

            return cost;
        }

        public String getProfit() {

            return profit;
        }

        public String getRepair() {

            return repair;
        }

        public String getDt() {

            return dt;
        }

    }