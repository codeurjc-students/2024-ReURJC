import { SubjectMark } from "src/app/services/UserService/SubjectMark";

export class StatefulNotifications {
  public notifications: SubjectMark;
  public nuevo: boolean = true;

  constructor(notifications: SubjectMark, nuevo: boolean) {
    this.notifications = notifications;
    this.nuevo = nuevo;
  }
}