export class SportReservation {
    sportReservationId: number;
    studentId: number; // Asumiendo que studentId es un número
    date: Date;
    pista: number;
  
    constructor(
      sportReservationId: number,
      studentId: number,
      date: Date,
      pista: number
    ) {
      this.sportReservationId = sportReservationId;
      this.studentId = studentId;
      this.date = date;
      this.pista = pista;
    }
  }