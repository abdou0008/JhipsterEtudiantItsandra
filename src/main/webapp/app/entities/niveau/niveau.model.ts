export interface INiveau {
  id?: number;
  nomNiveau?: string | null;
}

export class Niveau implements INiveau {
  constructor(public id?: number, public nomNiveau?: string | null) {}
}

export function getNiveauIdentifier(niveau: INiveau): number | undefined {
  return niveau.id;
}
