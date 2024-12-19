import { Schedule } from "./Schedule";

export class SubjectScheduleResponse {
  title: string;
  schedule: Schedule[];

  constructor(title: string, schedule: Schedule[]) {
    this.title = title;
    this.schedule = schedule;
  }

  getTitle(): string {
    return this.title
  }

  getSchedule(): Schedule[] {
    return this.schedule
  }
}