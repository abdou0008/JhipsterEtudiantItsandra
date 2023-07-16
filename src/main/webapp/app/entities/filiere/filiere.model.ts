export interface IFiliere {
  id?: number;
  nomFiliere?: string | null;
}

export class Filiere implements IFiliere {
  constructor(public id?: number, public nomFiliere?: string | null) {}
}

export function getFiliereIdentifier(filiere: IFiliere): number | undefined {
  return filiere.id;
}
