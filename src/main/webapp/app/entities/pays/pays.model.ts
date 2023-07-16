export interface IPays {
  id?: number;
  nomPays?: string | null;
}

export class Pays implements IPays {
  constructor(public id?: number, public nomPays?: string | null) {}
}

export function getPaysIdentifier(pays: IPays): number | undefined {
  return pays.id;
}
