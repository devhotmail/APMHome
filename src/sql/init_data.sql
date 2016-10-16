INSERT INTO user_account VALUES (1, 'admin', 'admin', '5605ee4b362e9754', NULL, NULL, NULL, '2016-10-03 11:08:56', true, true, NULL, NULL, false);

INSERT INTO chart_config VALUES (1, 'dashboard_pie', 'select * from test where id>=990 ', 1, 'Pie', 'rating', 'rental_rate', 'rental_rate', '饼图测试', 'e', NULL, 2, 1, 'x Label', NULL, NULL, NULL, NULL, NULL, NULL, 'y Label', NULL, NULL, 0);
INSERT INTO chart_config VALUES (2, 'dashboard_bar', 'select * from test where true :#searchFilter', 1, 'Bar', 'rating', 'date_str', 'rental_rate', '柱状图测试', 'e', NULL, 2, 1, 'x Label', NULL, NULL, NULL, NULL, NULL, NULL, 'y Label', NULL, NULL, 1);
INSERT INTO chart_config VALUES (3, 'dashboard_line', 'select * from test where true :#searchFilter', 1, 'Line', 'rating', 'date_str', 'rental_rate', '趋势图', 'e', NULL, 2, 0, 'X', '', '', '', '', '', '', 'YY', '', '', 1);


INSERT INTO data_table_config VALUES (4, 'test1', 'select count(*) from chart_config', 'select * from chart_config', 1, 1);
INSERT INTO data_table_config VALUES (5, 'test2', 'select count(*) from test', 'select * from test', 1, 1);
