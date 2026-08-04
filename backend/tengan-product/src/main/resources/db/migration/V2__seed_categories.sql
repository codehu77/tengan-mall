INSERT INTO category (id, parent_id, name, icon, sort, level, status) VALUES
    (1, 0, '3C 產品', 'ep:cpu', 1, 1, 1),
    (2, 0, '服飾配件', 'ep:shopping-bag', 2, 1, 1),
    (3, 0, '居家生活', 'ep:house', 3, 1, 1),
    (4, 1, '手機平板', 'ep:cellphone', 1, 2, 1),
    (5, 1, '筆記型電腦', 'ep:monitor', 2, 2, 1),
    (6, 2, '男裝', 'ep:man', 1, 2, 1),
    (7, 2, '女裝', 'ep:woman', 2, 2, 1),
    (8, 3, '廚房用品', 'ep:knife-fork', 1, 2, 1);
