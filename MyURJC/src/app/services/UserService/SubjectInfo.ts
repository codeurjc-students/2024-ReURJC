import { ConvocatoryInfo } from "./ConvocatoryInfo";

export class SubjectInfo {
    id: number;
    title: string = "";
    convocatory: ConvocatoryInfo[] = [];

    constructor(id: number, title: string, convocatory: ConvocatoryInfo[]) {
        this.title = title;
        this.convocatory = convocatory;
        this.id = id;
    }
}