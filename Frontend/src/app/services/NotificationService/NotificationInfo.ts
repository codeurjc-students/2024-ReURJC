import { User } from "../UserService/user.model";

export class NotificationInfo {
  public student: User;
  public title: string;
  public description: string;

  constructor(student: User, title: string, description: string) {
    this.student = student;
    this.title = title;
    this.description = description;
  }
}
