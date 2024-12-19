import { User } from "./user.model";

export interface SportReservation {
  sportReservationId: number;
  studentId: User;
  date: Date;
  pista: number;


}