package kr.osam.pavlov;

public class Mission {
    private int type;
    private String name;
    private int Id;
    private int achieveStatus = 0;

    Mission(int missionType, String missionName, int missionId)
    {
        this.type = missionType;
        this.Id = missionId;
        this.name = missionName;
    }

    public void setAchieveStatus(int achieve)
    {
        this.achieveStatus = achieve;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public int getAchieveStatus()
    {
        return this.achieveStatus;
    }
    public String getName()
    {
        return this.name;
    }
}
