export class ConvocatoryInfo {


    constructor(
        private convocatoryId: number,
        private date: string,
        private convocatory: number,
        private classroom: string
    ) {}

    // Getter para convocatoryId
    public getConvocatoryId(): number {
        return this.convocatoryId;
    }

    // Getter para date
    public getDate(): string {
        return this.date;
    }

    // Getter para convocatory
    public getConvocatory(): number {
        return this.convocatory;
    }

    // Getter para classroom
    public getClassroom(): string {
        return this.classroom;
    }

    

        


}