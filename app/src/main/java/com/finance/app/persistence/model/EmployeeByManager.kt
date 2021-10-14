package com.finance.app.persistence.model

import java.io.Serializable

class EmployeeByManager : Serializable {
   var employeeID: Int?=0
    var employeeName: String?=" "
    var employeeCode: String? =" "
    var baseBranchID: Int?=0
    var  departmentID: Int?=0
    var designationLevelTypeDetailID: Int?=0
    var reportLevelTypeDetailID:  Int?=0
    var parentEmployeeID: String?=" "
    var parentEmployeeName: String?=" "
    var relivingDate: String?=" "
    var joiningDate: String?=" "
    var dob: String?=" "
    var panNumber: String?=" "
    var bankNameTypeDetailId: String?=" "
    var bankName: String?=" "
    var ifscCode: String?=" "
    var gstNumber: String?=" "
    var bankAccountNumber: String?=" "
    var remarks: String?=" "
    var isActive: String?=" "
    var createdOn: String?=" "
    var lastModifiedOn: String?=" "
    var entityID: Int?=0
    var designationTypeDetailID: Int?=0
    var mobile: String?=" "
    var emailID: String?=" "
    var isResigned: String?=" "
    var salesforceEmpContactID: String?=" "
    var userID: Int=0

 override fun toString(): String {
  return employeeName.toString()
 }
}