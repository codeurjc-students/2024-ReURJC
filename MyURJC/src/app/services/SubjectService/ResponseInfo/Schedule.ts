export class Schedule {
  // Properties with type annotations
  dayOfWeek: number;
  startHour: number;
  endHour: number;
  classRoom: String;

  // Constructor with parameters and type annotations
  constructor(dayOfWeek: number, startHour: number, endHour: number, classRoom: String) {
    this.dayOfWeek = dayOfWeek;
    this.startHour = startHour;
    this.endHour = endHour;
    this.classRoom = classRoom;
  }


  // Getters with no logic (just return the property)
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