import dayjs from 'dayjs/esm';
import { INiveau } from 'app/entities/niveau/niveau.model';
import { IFiliere } from 'app/entities/filiere/filiere.model';
import { IPays } from 'app/entities/pays/pays.model';

export interface IEtudiant {
  id?: number;
  nom?: string | null;
  prenom?: string | null;
  matricule?: number | null;
  date?: dayjs.Dayjs | null;
  nomNiveau?: INiveau | null;
  nomFiliere?: IFiliere | null;
  nomPays?: IPays | null;
}

export class Etudiant implements IEtudiant {
  constructor(
    public id?: number,
    public nom?: string | null,
    public prenom?: string | null,
    public matricule?: number | null,
    public date?: dayjs.Dayjs | null,
    public nomNiveau?: INiveau | null,
    public nomFiliere?: IFiliere | null,
    public nomPays?: IPays | null
  ) {}
}

export function getEtudiantIdentifier(etudiant: IEtudiant): number | undefined {
  return etudiant.id;
}
