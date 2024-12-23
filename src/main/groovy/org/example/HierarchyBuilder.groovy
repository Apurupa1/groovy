package org.example

class HierarchyBuilder {
    static Department build(Map<String, Department> departmentMap) {
        Department root = null
        departmentMap.values().each { department ->
            if (!department.parentId) {
                root = department
            } else {
                Department parent = departmentMap[department.parentId]
                parent?.subDepartments << department
            }
        }
        return root
    }
}