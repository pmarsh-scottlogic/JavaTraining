type FakeAccount = {
    name: string;
    id: string;
};

export const hardAccounts: Array<FakeAccount> = [
    makeAccount('Gerald', '1a7eda79-8942-4bfb-af64-f37ba68cbee4'),
    makeAccount('Martha', '08d9dc73-2a4b-42b9-9a7f-6f278d015edc'),
    makeAccount('Janet', '22a51d64-e077-4352-9ff9-e92933706425'),
];

function makeAccount(name: string, id: string) {
    return {
        name: name,
        id: id,
    } as FakeAccount;
}
