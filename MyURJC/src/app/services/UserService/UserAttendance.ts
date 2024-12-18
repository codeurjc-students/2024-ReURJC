import { Attendance } from "./Attendance";
import { User } from "./user.model";

export interface UserAttendance {
  user: User;
  attendance: Attendance;


}