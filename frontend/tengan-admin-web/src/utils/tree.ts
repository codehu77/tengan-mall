/**
 * @description 提取選單樹中的每一項uniqueId
 * @param tree 樹
 * @returns 每一項uniqueId組成的陣列
 */
export const extractPathList = (tree: any[]): any => {
  if (!Array.isArray(tree)) {
    console.warn("tree must be an array");
    return [];
  }
  if (!tree || tree.length === 0) return [];
  const expandedPaths: Array<number | string> = [];
  for (const node of tree) {
    const hasChildren = node.children && node.children.length > 0;
    if (hasChildren) {
      extractPathList(node.children);
    }
    expandedPaths.push(node.uniqueId);
  }
  return expandedPaths;
};

/**
 * @description 如果父級下children的length為1，刪除children並自動組建唯一uniqueId
 * @param tree 樹
 * @param pathList 每一項的id組成的陣列
 * @returns 元件唯一uniqueId後的樹
 */
export const deleteChildren = (tree: any[], pathList = []): any => {
  if (!Array.isArray(tree)) {
    console.warn("menuTree must be an array");
    return [];
  }
  if (!tree || tree.length === 0) return [];
  for (const [key, node] of tree.entries()) {
    if (node.children && node.children.length === 1) delete node.children;
    node.id = key;
    node.parentId = pathList.length ? pathList[pathList.length - 1] : null;
    node.pathList = [...pathList, node.id];
    node.uniqueId =
      node.pathList.length > 1 ? node.pathList.join("-") : node.pathList[0];
    const hasChildren = node.children && node.children.length > 0;
    if (hasChildren) {
      deleteChildren(node.children, node.pathList);
    }
  }
  return tree;
};

/**
 * @description 建立層級關係
 * @param tree 樹
 * @param pathList 每一項的id組成的陣列
 * @returns 建立層級關係後的樹
 */
export const buildHierarchyTree = (tree: any[], pathList = []): any => {
  if (!Array.isArray(tree)) {
    console.warn("tree must be an array");
    return [];
  }
  if (!tree || tree.length === 0) return [];
  for (const [key, node] of tree.entries()) {
    node.id = key;
    node.parentId = pathList.length ? pathList[pathList.length - 1] : null;
    node.pathList = [...pathList, node.id];
    const hasChildren = node.children && node.children.length > 0;
    if (hasChildren) {
      buildHierarchyTree(node.children, node.pathList);
    }
  }
  return tree;
};

/**
 * @description 廣度優先遍歷，根據唯一uniqueId找當前節點資訊
 * @param tree 樹
 * @param uniqueId 唯一uniqueId
 * @returns 當前節點資訊
 */
export const getNodeByUniqueId = (
  tree: any[],
  uniqueId: number | string
): any => {
  if (!Array.isArray(tree)) {
    console.warn("menuTree must be an array");
    return [];
  }
  if (!tree || tree.length === 0) return [];
  const item = tree.find(node => node.uniqueId === uniqueId);
  if (item) return item;
  const childrenList = tree
    .filter(node => node.children)
    .map(i => i.children)
    .flat(1) as unknown;
  return getNodeByUniqueId(childrenList as any[], uniqueId);
};

/**
 * @description 向當前唯一uniqueId節點中追加欄位
 * @param tree 樹
 * @param uniqueId 唯一uniqueId
 * @param fields 需要追加的欄位
 * @returns 追加欄位後的樹
 */
export const appendFieldByUniqueId = (
  tree: any[],
  uniqueId: number | string,
  fields: object
): any => {
  if (!Array.isArray(tree)) {
    console.warn("menuTree must be an array");
    return [];
  }
  if (!tree || tree.length === 0) return [];
  for (const node of tree) {
    const hasChildren = node.children && node.children.length > 0;
    if (
      node.uniqueId === uniqueId &&
      Object.prototype.toString.call(fields) === "[object Object]"
    )
      Object.assign(node, fields);
    if (hasChildren) {
      appendFieldByUniqueId(node.children, uniqueId, fields);
    }
  }
  return tree;
};

/**
 * @description 構造樹型結構資料
 * @param data 資料來源
 * @param id id欄位 預設id
 * @param parentId 父節點欄位，預設parentId
 * @param children 子節點欄位，預設children
 * @returns 追加欄位後的樹
 */
export const handleTree = (
  data: any[],
  id?: string,
  parentId?: string,
  children?: string
): any => {
  if (!Array.isArray(data)) {
    console.warn("data must be an array");
    return [];
  }
  const config = {
    id: id || "id",
    parentId: parentId || "parentId",
    childrenList: children || "children"
  };

  const childrenListMap: any = {};
  const nodeIds: any = {};
  const tree = [];

  for (const d of data) {
    const parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  for (const d of data) {
    const parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  for (const t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o: Record<string, any>) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (const c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
};
