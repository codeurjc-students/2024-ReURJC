import { SubjectInfo } from "./SubjectInfo";
import { User } from "./user.model";
import { UserAttendance } from "./UserAttendance";

export interface Attendance {
    id: number
    dateTime: Date;
    creator: User;
    subject: SubjectInfo;
    code: string;
    usersPresent: UserAttendance[];


}