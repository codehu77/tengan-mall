ALTER TABLE base_attr
    ADD COLUMN unit VARCHAR(10) NULL COMMENT '選填單位，例如"吋"；填值時只需輸入數字，顯示時自動帶上' AFTER name;

ALTER TABLE sale_attr
    ADD COLUMN unit VARCHAR(10) NULL COMMENT '選填單位，例如"GB"；填值時只需輸入數字，顯示時自動帶上' AFTER name;
