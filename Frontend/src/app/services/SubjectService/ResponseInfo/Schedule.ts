export class Schedule {

  dayOfWeek: number;
  startHour: number;
  endHour: number;
  classRoom: String;


  constructor(dayOfWeek: number, startHour: number, endHour: number, classRoom: String) {
    this.dayOfWeek = dayOfWeek;
    this.startHour = startHour;
    this.endHour = endHour;
    this.classRoom = classRoom;
  }


  getDayOfWeek(): number {
    return this.dayOfWeek;
  }

  getStartHour(): number {
    return this.startHour;
  }

  getEndHour(): number {
    return this.endHour;
  }

  getClassRoom(): String {
    return this.classRoom;
  }
}
