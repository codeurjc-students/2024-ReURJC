import { SubjectInfo } from "./SubjectInfo";
import { User } from "./user.model";

export class SubjectMark {
    subjectMarkId: number;
    studentId: User; 
    subjectId: SubjectInfo;
    mark: number;
    convocatory: string;
    nameMark: string;
  
    constructor(
        subjectMarkId: number,
        studentId: User,
        subjectId: SubjectInfo,
        mark: number,
        convocatory: string,
        nameMark: string
    ) {
      this.subjectMarkId = subjectMarkId;
      this.studentId = studentId;
      this.subjectId = subjectId;
      this.mark = mark;
      this.convocatory = convocatory;
      this.nameMark = nameMark;
    }
  }