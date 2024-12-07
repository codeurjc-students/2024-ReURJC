export class FestiveInfo {
    day: number;
    month: number;
    year: number;
    color: string;
    startedXDaysAgo: number;
    local: string

    constructor(day: number, month: number, year: number, color: string, startedXDaysAgo: number, local: string) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.color = color;
        this.startedXDaysAgo = startedXDaysAgo;
        this.local = local;



    }

    static generateFestivesFromRange(festive: FestiveInfo): FestiveInfo[] {
        const festiveList: FestiveInfo[] = [];

        // Crear la fecha final (el día del festivo especificado)
        const endDate = new Date(festive.year, festive.month - 1, festive.day);

        // Calcular la fecha de inicio restando los xdaysAgo
        const startDate = new Date(endDate);
        startDate.setDate(endDate.getDate() - festive.startedXDaysAgo);

        // Iterar desde la fecha de inicio hasta la fecha final
        let currentDate = new Date(startDate);
        while (currentDate <= endDate) {
            // Crear un nuevo objeto FestiveInfo para cada día
            festiveList.push(new FestiveInfo(
                currentDate.getDate(),
                currentDate.getMonth() + 1, // Ajustar el mes al rango 1-12
                currentDate.getFullYear(),
                festive.color, // Usar el mismo color
                festive.startedXDaysAgo,
                festive.local // Usar la misma localidad
            ));

            // Avanzar un día
            currentDate.setDate(currentDate.getDate() + 1);
        }
        return festiveList;
    }


}

